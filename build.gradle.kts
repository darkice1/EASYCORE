@file:Suppress("VulnerableLibrariesLocal")

import java.nio.charset.MalformedInputException

plugins {
	kotlin("jvm") version "2.1.20"
	`java-library`
	id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
	`maven-publish`
	signing
}

group = "com.github.darkice1"
version = "1.0.96"
val projectName = "easy"
val projectDesc = "Neo easy code."

// ---------------- Java toolchain & 附件 JAR ----------------
java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
	withSourcesJar()
	withJavadocJar()
}

// ---------------- 仓库 ----------------
repositories {
	mavenCentral()
	mavenLocal()
}

// ---------------- 依赖 ----------------
dependencies {
	// 常规依赖（Maven 默认 compile）
	// https://mvnrepository.com/artifact/com.zaxxer/HikariCP
	api("com.zaxxer:HikariCP:7.0.2")
	// https://mvnrepository.com/artifact/com.esotericsoftware.kryo/kryo5
	api("com.esotericsoftware.kryo:kryo5:5.6.2")
	// https://mvnrepository.com/artifact/org.apache.httpcomponents.client5/httpclient5
	api("org.apache.httpcomponents.client5:httpclient5:5.5")
	api("de.huxhorn.lilith:de.huxhorn.lilith.3rdparty.junique:1.0.4")
	// https://mvnrepository.com/artifact/org.htmlunit/htmlunit
	api("org.htmlunit:htmlunit:4.12.0")
	api("com.googlecode.juniversalchardet:juniversalchardet:1.0.3")
	api("org.json:json:20250107")
	api("com.github.mwiede:jsch:0.2.22")
	api("javax.mail:mail:1.4.7")
//	api("org.apache.commons:commons-pool2:2.12.0")
	api(kotlin("stdlib"))

	val tomcatVersion = "11.0.8"
	compileOnly("org.apache.tomcat:tomcat-catalina:$tomcatVersion")
	compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
	compileOnly("jakarta.servlet.jsp:jakarta.servlet.jsp-api:3.1.0")
	compileOnly("jakarta.el:jakarta.el-api:5.0.1")
	compileOnly("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:3.0.0")

	// 测试
	testImplementation(kotlin("test"))
}

// ---------------- Kotlin 编译选项 ----------------
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
	compilerOptions {
		jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
	}
}

// ---------------- Javadoc 选项 ----------------
tasks.withType<Javadoc>().configureEach {
	(options as StandardJavadocDocletOptions).apply {
		encoding = "UTF-8"
		docEncoding = "UTF-8"
		addStringOption("Xdoclint:none", "-quiet")
	}
}

// ---------------- 发布到 OSSRH ----------------
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
			artifactId = projectName

			pom {
				name.set(projectName)
				description.set(projectDesc)
				url.set("https://github.com/darkice1/$projectName")

				licenses {
					license {
						name.set("Apache License 2.0")
						url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
					}
				}
				developers {
					developer {
						id.set("neo")
						name.set("neo")
						email.set("starneo@gmail.com")
					}
				}
				scm {
					connection.set("scm:git:https://github.com/darkice1/$projectName.git")
					url.set("https://github.com/darkice1/$projectName")
				}
			}
		}
	}
}

val coords = "${project.group}:$projectName:$version"
tasks.register("publishAndCloseSonatype") {
	group = "mypublishing"
	description =
		"Publish artifacts to Sonatype OSSRH, then close the staging repository."
	dependsOn("publishToSonatype", "closeSonatypeStagingRepository")
//	finalizedBy("closeSonatypeStagingRepository")// 上传完成后再执行 close
	doLast {
		println("close:[$coords]")
	}
}

tasks.register("publiclocal") {
	group = "mypublishing"
	description = "Close & release Sonatype staging repo, then print coordinates."
	dependsOn("publishMavenJavaPublicationToMavenLocal")
	doLast {
		println("public local:[$coords]")
	}
}

tasks.register("release") {
	group = "mypublishing"
	description = "Close & release Sonatype staging repo, then print coordinates."
	dependsOn("publishToSonatype", "closeAndReleaseSonatypeStagingRepository")
	doLast {
		println("release:[$coords]")
	}
}

tasks.register("checkSensitiveConfig") {
	group = "verification"
	description = "Check tracked files for obvious database credentials and tokens."

	doLast {
		val gitLsFiles = ProcessBuilder("git", "ls-files")
			.directory(rootDir)
			.redirectErrorStream(true)
			.start()
		val trackedOutput = gitLsFiles.inputStream.bufferedReader(Charsets.UTF_8).readText()
		val exitValue = gitLsFiles.waitFor()
		if (exitValue != 0) {
			logger.warn("Skip checkSensitiveConfig because git ls-files failed.")
			return@doLast
		}

		val allowedSecretValue = Regex(
			"""(?i)^(?:|your[_-].*|changeme|change_me|placeholder|example|<.*>|\$\{.*})$"""
		)
		val keyValuePattern = Regex(
			"""^\s*([A-Za-z0-9_]*(?:DBPASSWORD|LOGPASSWORD|TOKEN|SECRET|APIKEY|API_KEY))\s*=\s*["']?([^"']*)["']?\s*$""",
			RegexOption.IGNORE_CASE
		)
		val urlCredentialPattern = Regex(
			"""(?i)(?:api[_-]?key|token|secret)=([^&\s"']+)"""
		)
		val dbRootPattern = Regex("""(?i)^\s*[A-Za-z0-9_]*DBUSER\s*=\s*root\s*$""")
		val literalMysqlIpPattern = Regex("""(?i)jdbc:mysql://(?:\d{1,3}\.){3}\d{1,3}""")
		val multiQueryPattern = Regex("""(?i)allowMultiQueries\s*=\s*true""")
		val findings = mutableListOf<String>()
		val trackedFiles = trackedOutput
			.lineSequence()
			.filter { it.isNotBlank() }
			.map { project.file(it) }
			.filter { it.isFile && it.length() <= 2_000_000 }
			.toList()

		trackedFiles.forEach { trackedFile ->
			try {
				trackedFile.useLines(Charsets.UTF_8) { lines ->
					lines.forEachIndexed { index, line ->
						val trimmed = line.trim()
						val uncommented = trimmed
							.removePrefix("#")
							.removePrefix("//")
							.trim()

						val kv = keyValuePattern.matchEntire(uncommented)
						if (kv != null && !allowedSecretValue.matches(kv.groupValues[2].trim())) {
							findings.add("${trackedFile.path}:${index + 1}: secret-like value for ${kv.groupValues[1]}")
						}
						urlCredentialPattern.findAll(uncommented).forEach { credential ->
							val value = credential.groupValues[1].trim()
							if (!allowedSecretValue.matches(value)) {
								findings.add("${trackedFile.path}:${index + 1}: URL credential-like value is not a placeholder")
							}
						}
						if (dbRootPattern.containsMatchIn(uncommented)) {
							findings.add("${trackedFile.path}:${index + 1}: database user is root")
						}
						if (literalMysqlIpPattern.containsMatchIn(uncommented)) {
							findings.add("${trackedFile.path}:${index + 1}: MySQL URL uses a literal IPv4 address")
						}
						if (multiQueryPattern.containsMatchIn(uncommented)) {
							findings.add("${trackedFile.path}:${index + 1}: multi-query JDBC option is enabled")
						}
					}
				}
			} catch (_: MalformedInputException) {
				// Binary tracked file.
			}
		}

		if (findings.isNotEmpty()) {
			throw GradleException("Sensitive config check failed:\n" + findings.joinToString("\n"))
		}
	}
}

tasks.named("check") {
	dependsOn("checkSensitiveConfig")
}

nexusPublishing {
	repositories {
		sonatype {
			nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
			snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
			username.set(providers.gradleProperty("centralUsername"))
			password.set(providers.gradleProperty("centralPassword"))
		}
	}
}

// ---------------- GPG 签名 ----------------
signing {
	val inMemKey: String? = providers.gradleProperty("signingKey").orNull
	val inMemPwd: String? = providers.gradleProperty("signingPassword").orNull

	when {
		inMemKey != null && inMemPwd != null -> useInMemoryPgpKeys(inMemKey, inMemPwd)
		else -> useGpgCmd()
	}

	sign(publishing.publications["mavenJava"])
}
// # 仅上传上传并 Close
// ./gradlew publishToSonatype closeSonatypeStagingRepository
// 上传发布
// ./gradlew closeAndReleaseSonatypeStagingRepository
