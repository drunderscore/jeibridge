# JEIBridge
A network bridge between the JEI Forge mod and a Paper server.

Licensed under GNU GPL Version 3

## Install
Plop this jar into your plugins.  
Requires ProtocolLib.

## Use
Grant the `jeibridge.cheat` permission to use cheat mode.  
Simple crafting completion is available.

## API
Listen for `GiveItemEvent` to know when a player clicks an item.  
Listen for `RequestCheatPermissoinEvent` to know when a player enables cheat mode.  
Listen for `SetHotbarItemEvent` to know when a player sets an item in their hotbar with hotkeys.  

Use `JEIBridge#sendCheatPermission` to inform the user of their cheat permission. This doesn't
necessarily enable or disable cheat mode -- it's just informing the client.
