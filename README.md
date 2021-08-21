# mine.js(Developing)
Minecraft Java Edition Scripting with JS(V8 Engine) by Netherald
## How to apply?
Download Paper or Bungee Version. and put it to plugins folder.\
Script folder is `plugins/js`!
## How to build?
Run build task of Bukkit or Bungee Submodule!
## How to use?
[Wiki](http://netherald.org:3000)
### Example code
```js
let a = 'yep'
function onInit() {
  a = storage.get('test')
  createCommand('hello', [], callback)
  console.log('ok init succ')
}

function onPlayerMove(event) {
  if(event.player.name === 'hiworld') {
    event.player.send('<red>Don\'t move!')
    event.setCancelled(true)
  }
}

function callback(args, sender) {
  sender.send('hello')
  if(sender.type === 'type') {
    sender.getPlayer().teleport(location.create('world', 1, 2, 3))
  }
}
```
