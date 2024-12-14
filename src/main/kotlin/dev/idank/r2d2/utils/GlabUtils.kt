package dev.idank.r2d2.utils

import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern

object GlabUtils {
    fun getGitlabToken(instance: String): String? {
        val configPath = Paths.get(System.getProperty("user.home"), ".config", "glab-cli", "config.yml")
        if (Files.exists(configPath)) {
            var foundInstance = false
            var token: String? = null

            for (line in Files.lines(configPath)) {
                if (line.contains(instance)) {
                    foundInstance = true
                }

                if (foundInstance && line.contains("token:")) {
                    val pattern = Pattern.compile("token:\\s*(\\S+)")
                    val matcher = pattern.matcher(line)
                    if (matcher.find()) {
                        token = matcher.group(1)
                        break
                    }
                }
            }

            return token
        }

        return null
    }
}