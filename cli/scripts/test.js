function onInit() {
    console.log('creating commands...')
    createCommand('test', ['t', 't2'], (ctx) => {
        console.log(ctx)
    })
    console.log('Hello World2!')
}

function onPlayerMove(event) {
    //console.log(`FromX: ${event.from.x}, FromY: ${event.from.x}, FromX: ${event.from.x}`)
    //console.log(`Player: ${event.player.name}`)
    //console.log(playerOf("netherald"))
    console.log(`Platform: ${getPlatform()}`)
    event.setCancelled(true)
}