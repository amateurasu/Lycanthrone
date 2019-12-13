import org.gradle.internal.os.OperatingSystem

extra["os"] = OperatingSystem.current()

extra["npm"] = if ((extra["os"] as OperatingSystem).isWindows) {
    "C:\\Program Files\\nodejs\\npm.cmd"
} else {
    ""
}
