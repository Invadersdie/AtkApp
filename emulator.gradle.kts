import org.apache.tools.ant.taskdefs.condition.Os
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

open class EmulatorsPluginExtension {
    var name: String? = null
    var cpuCores: Int = 1
    var stopEmulatorAfter: Array<String>? = null
}

private class Config(
    private val sdkDir: String
) {
    val emulator = sdkFile("emulator", platformExecutable("emulator"))

    val adb = sdkFile("platform-tools", platformExecutable("adb"))

    private fun sdkFile(vararg path: String) = File(sdkDir, path.joinToString(File.separator))

    private fun platformExecutable(name: String): String {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "$name.exe"
        }
        return name
    }
}

class EmulatorsPlugin : Plugin<Project> {

    private lateinit var project: Project

    override fun apply(project: Project) {
        this.project = project
        val extension = project.extensions.create<EmulatorsPluginExtension>("emulators")
        createStartAndStopEmulatorsTask(project, extension, Config(readAndroidSdkLocation()))
    }

    private fun createStartAndStopEmulatorsTask(
        project: Project,
        extention: EmulatorsPluginExtension,
        config: Config
    ) {
        project.afterEvaluate {
            val startAndStopAllEmulators = project.tasks.create("startAndStopEmulators")
            val emulatorName = extention.name
            if (emulatorName != null) {
                val pb = ProcessBuilder(
                    config.emulator.absolutePath,
                    "-avd",
                    emulatorName,
                    "-cores",
                    extention.cpuCores.toString(),
                    "-no-window",
                    "-noaudio"
                )
                val startEmulatorTask = project.tasks.create("startEmulator") {
                    doFirst {
                        debug("Start with command: ${pb.command()}")
                        val process = pb.start()
                        Thread.sleep(1000)
                        if (process.isAlive.not() && process.exitValue() != 0) {
                            error(
                                "Can't start emulator:\n ${pb.command()}\n" +
                                        "${readTextFromStream(process.inputStream)}\n" +
                                        readTextFromStream(process.errorStream)
                            )
                            throw Error("Failed to start emulator")
                        }
                    }
                }
                val waitForEmulator = createExecTask("waitForEmulator") {
                    executable = config.adb.absolutePath
                    setArgs(
                        listOf(
                            "wait-for-device",
                            "shell",
                            "while $(exit $(getprop sys.boot_completed)) ; do sleep 1; done;"
                        )
                    )
                    dependsOn(startEmulatorTask)
                }
                val stopEmulatorTask = createExecTask("stopEmulator") {
                    executable = config.adb.absolutePath
                    setArgs(listOf("emu", "kill"))
                    if (extention.stopEmulatorAfter != null) {
                        mustRunAfter(extention.stopEmulatorAfter)
                    }
                }
                startEmulatorTask.finalizedBy(stopEmulatorTask)
                startAndStopAllEmulators.dependsOn(waitForEmulator)
            }
        }
    }

    private fun createExecTask(name: String, configuration: Exec.() -> Unit): Task {
        return project.tasks.create(name, Exec::class.java) {
            configuration(this)
        }
    }

    private fun debug(message: String) {
        project.logger.debug("EmulatorsPlugin: $message")
    }

    private fun error(message: String) {
        project.logger.error(message)
    }

    private fun readAndroidSdkLocation(): String {
        var result = readAndroidSdkFromLocalProperties()
        if (result == null) {
            result = System.getenv("ANDROID_HOME")
        }
        if (result == null) {
            throw Error("Android sdk isn't defined in local properties or environment variable")
        }
        return result
    }

    private fun readAndroidSdkFromLocalProperties(): String? {
        val rootDir = project.rootDir
        val localProperties = File(rootDir, "local.properties")
        if (localProperties.exists()) {
            val properties = Properties()
            properties.load(localProperties.inputStream())
            return properties.getProperty("sdk.dir")
        }
        return null
    }

    private fun readTextFromStream(stream: InputStream): String {
        val reader = InputStreamReader(stream, "UTF-8")
        val result = StringBuilder()
        val buffer = CharArray(256)
        var readed = reader.read(buffer, 0, buffer.size)
        while (readed > 0) {
            result.append(buffer, 0, readed)
            readed = reader.read(buffer, 0, buffer.size)
        }
        return result.toString()
    }
}

apply<EmulatorsPlugin>()