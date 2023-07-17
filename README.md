# MiniMods

MiniMods is a mod loader for Minicraft+. To learn more about Minicraft+, you may go to [Minicraft GitHub repository](https://github.com/MinicraftPlus/minicraft-plus-revived).

## License

This repository is licensed with LGPL 2.1 and GPL 3, you should be able to find the corresponding license documents in the same directory of this source code. The overall license is named [`LICENSE` in the project's root directory](/LICENSE).

`SPDX-License-Identifier: LGPL-2.1-only AND GPL-3-only`

## Officially Supported Mods

There are some MiniMods officially supported mods that are available.

### API/Core Mods

These mods are intended to be an modding API for supporting further modding experience and convenience.

#### [Ores API](https://github.com/AnvilloyDevStudio/MiniMods-Mod-Core-Ores)

Core-Ores provides basic API functions for adding ores to Minicraft+.

### Other

These mods are not intended to be an modding API, but provide extra features.

#### [Redstone](https://github.com/AnvilloyDevStudio/MiniMods-Mod-Core-Redstone)

Core-Redstone is a mod for the redstone system referenced from Minecraft. This has some similar mechanics from it.

#### [Redstone Extra](https://github.com/AnvilloyDevStudio/MiniMods-Mod-Core-Redstone-Extra)

Core-Redstone-Extra is a mod intended to expand functionalities with Core-Redstone.

This is an archived project derived from Core-Redstone.

## Building

As there are several batch files in this project, it is recommended to use Windows.

To build this project, the most recommended way is to use the [local batch file](build.bat). Use `.\build build` for Windows.

Since there are some potential problem when executing general `.\gradlew build`. Please execute `.\gradlew :build` instead.
