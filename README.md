# R2D2 - IntelliJ TODO Handler Plugin

R2D2 is an IntelliJ-based plugin that enhances the way you handle TODO comments in your codebase. 

> ⚠️ **Development Status**: This plugin is currently in development stage and has not been officially submitted to the JetBrains Marketplace. Installation is only available through manual ZIP file deployment.

## Features
- Github and Gitlab issue creation support
- When writing a todo the plugin will extract the title and description from the todo and will assist you to create an issue from the intellij.

## Requirements
- IntelliJ IDEA version: *2024+*
  
## Installation

Since this plugin is not yet available in the JetBrains Marketplace, follow these steps for manual installation:

1. Download the latest plugin ZIP file from the [Releases](https://github.com/IdanKoblik/R2D2/releases) tab
2. In IntelliJ IDEA, go to `Settings/Preferences → Plugins`
3. Click on the gear icon (⚙️) and select `Install Plugin from Disk...`
4. Navigate to the downloaded ZIP file and select it
5. Restart IntelliJ IDEA when prompted

## Supported TODO types
- Kotlin TODO()
- Normal todo
- Bulk todo

> A todo description is the following line after the todo line except whitespace.
