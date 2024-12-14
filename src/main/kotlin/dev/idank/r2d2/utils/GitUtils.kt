package dev.idank.r2d2.utils

import java.io.File
import java.io.IOException

object GitUtils {
    fun extractGitInfo(repoPath: String): Pair<String, String> {
        var host = ""
        var namespace = ""

        val configFile = File("$repoPath/.git/config")

        if (!configFile.exists())
            throw IOException("Cannot find git config file")

        val configContent = configFile.readLines()
        for (line in configContent) {
            if (line.trim().startsWith("url =")) {
                val url = line.substringAfter("url =").trim()

                val sshRegex = "git@([a-zA-Z0-9.-]+):(.*?)(\\.git)?$".toRegex()
                val httpsRegex = "https://([a-zA-Z0-9.-]+)/(.+?)(\\.git)?$".toRegex()

                val sshMatch = sshRegex.matchEntire(url)
                if (sshMatch != null) {
                    host = sshMatch.groupValues[1]
                    namespace = sshMatch.groupValues[2]
                    break
                }

                val httpsMatch = httpsRegex.matchEntire(url)
                if (httpsMatch != null) {
                    host = httpsMatch.groupValues[1]
                    namespace = httpsMatch.groupValues[2]
                    break
                }
            }
        }

        return Pair(host, namespace)
    }
}

