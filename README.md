<div align="center">

![](https://avatars.githubusercontent.com/u/94840691?s=200&v=4)

![](https://img.shields.io/github/stars/mine-js/mine.js?style=for-the-badge) ![](https://img.shields.io/github/forks/mine-js/mine.js?style=for-the-badge) [![](https://img.shields.io/discord/912313035639627806?label=discord&style=for-the-badge)](https://discord.gg/ngpsJ4Sfmb)

# Mine.js(W-I-P)

Minecraft Bukkit Scripting with JS by Netherald
</div>

## How to apply?
Download Paper or Bungee Version. and put it to the plugins folder.\
Script folder is `plugins/js`!
## How to build?
Run build task of Bukkit or Bungee module!
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
    sender.player().teleport(location.create('world', 1, 2, 3))
  }
}
```

umm sib
