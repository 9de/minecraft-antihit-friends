# 🛡️ AntiHit Friend System

A Minecraft Forge mod that prevents you from accidentally hitting your friends during combat. Perfect for PvP scenarios where you want to protect specific players from friendly fire.

## Features

- Prevents hitting designated friends in-game
- Easy friend management with commands
- Toggle system on/off with a customizable keybind (default: R)
- Persistent friend list that saves between game sessions
- Chat notifications for all actions
- Tab completion support for commands

## Commands

- `/ahf friend add <username>` - Add a player to your friends list
- `/ahf friend remove <username>` - Remove a player from your friends list
- `/ahf friend list` - Display all players in your friends list
- `/ahf friend toggle` - Enable/disable the anti-hit system

## Installation

1. Make sure you have Minecraft Forge installed
2. Download the latest release from the releases page
3. Place the `.jar` file in your Minecraft `mods` folder
4. Launch Minecraft with the Forge profile

## Configuration

- Friend list is stored in `config/antihitfriend.txt`
- Default toggle key is 'R' - can be changed in Minecraft controls settings

## Building from Source

1. Clone the repository
2. Set up your development environment with Forge MDK
3. Run `./gradlew build` (or `gradlew.bat build` on Windows)
4. Find the compiled jar in `build/libs`

## Contributing

Feel free to submit issues and pull requests with improvements or bug fixes.

## License

This project is released under the MIT License. See the LICENSE file for details.

## Credits

Created for the Minecraft modding community. Special thanks to the Forge team for their modding framework.
