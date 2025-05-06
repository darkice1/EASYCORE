@file:Suppress("VulnerableLibrariesLocal")

plugins {
	kotlin("jvm") version "2.1.20"
	`java-library`
//	id("org.jetbrains.dokka") version "2.0.0"
	id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
	`maven-publish`
	signing
}

// ---------------- Dokka v2 ----------------
//dokka {
//    // Keep helpers during migration; switch to V2Enabled when all V1 APIs are removed
//    dokkaPublications.html {
//        outputDirectory.set(layout.buildDirectory.dir("dokka"))
//    }
//}

group = "com.github.darkice1"
version = "1.0.80"
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
	implementation("com.zaxxer:HikariCP:4.0.3")
	implementation("javax.servlet:jstl:1.2")
	implementation("com.esotericsoftware:kryo:4.0.2")
	implementation("org.apache.httpcomponents:httpclient:4.5.13")
	implementation("de.huxhorn.lilith:de.huxhorn.lilith.3rdparty.junique:1.0.4")
	implementation("org.htmlunit:htmlunit:3.11.0")
	implementation("com.googlecode.juniversalchardet:juniversalchardet:1.0.3")
	implementation("org.json:json:20250107")
	implementation("com.github.mwiede:jsch:0.2.22")
	implementation("org.apache.commons:commons-pool2:2.12.0")
	implementation("javax.jdo:jdo-api:3.1")
	implementation("org.xerial:sqlite-jdbc:3.41.2.2")
	implementation("javax.mail:mail:1.4.7")
	implementation("commons-fileupload:commons-fileupload:1.5") {
		exclude(group = "commons-io", module = "commons-io")
	}
	implementation(kotlin("stdlib"))

	// Maven <scope>provided</scope> → Gradle compileOnly
	compileOnly("javax.servlet:javax.servlet-api:3.1.0")
	compileOnly("javax.servlet.jsp:javax.servlet.jsp-api:2.3.0")
	compileOnly("javax.el:javax.el-api:3.0.0")

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

//	repositories {
//		// ---- Central Publisher Portal (OSSRH Staging API) ----
//		maven {
//			name = "ossrh-staging-api"
//			url = uri(
//				if (version.toString().endsWith("SNAPSHOT"))
//					"https://central.sonatype.com/repository/maven-snapshots/"
//				else
//					"https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/"
//			)
//			credentials {
//				username = providers.gradleProperty("centralUsername")
//					.orElse(providers.environmentVariable("CENTRAL_USERNAME"))
//					.getOrNull()
//				password = providers.gradleProperty("centralPassword")
//					.orElse(providers.environmentVariable("CENTRAL_PASSWORD"))
//					.getOrNull()
//			}
//		}
//	}
}

// ---------------- 快捷任务：上传并立即 Close ----------------
tasks.register("publishAndCloseSonatype") {
	group = "mypublishing"
	description =
		"Publish artifacts to Sonatype OSSRH, then close the staging repository."
	dependsOn("publishToSonatype","closeSonatypeStagingRepository")
//	finalizedBy("closeSonatypeStagingRepository")   // 上传完成后再执行 close
	doLast {
		println("close:[$group:$projectName:$version]")
	}
}

tasks.register("publiclocal") {
	group = "mypublishing"
	description = "Close & release Sonatype staging repo, then print coordinates."
	dependsOn("publishMavenJavaPublicationToMavenLocal")
	doLast {
		println("public local:[${project.group}:${project.name}:${project.version}]")
	}
}

tasks.register("release") {
	group = "mypublishing"
	description = "Close & release Sonatype staging repo, then print coordinates."
	dependsOn("publishToSonatype","closeAndReleaseSonatypeStagingRepository")
	doLast {
		println("release:[${project.group}:${project.name}:${project.version}]")
	}
}

nexusPublishing {
	repositories {
		sonatype {
			nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
			snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
			username.set(providers.gradleProperty("centralUsername"))
			password.set(providers.gradleProperty("centralPassword"))
		}
//		repositoryDescription = "$group:$projectName:$version"
//		description = "$group:$projectName:$version"
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
// # 仅上传   上传并 Close
// ./gradlew publishToSonatype closeSonatypeStagingRepository
// 上传发布
// ./gradlew closeAndReleaseSonatypeStagingRepository