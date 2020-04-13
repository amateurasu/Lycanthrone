import org.gradle.internal.os.OperatingSystem

val os: OperatingSystem = OperatingSystem.current()

extra["os"] = os

extra["npm"] = if (os.isWindows) {
    "C:\\Program Files\\nodejs\\npm.cmd"
} else {
    "npm"
}

