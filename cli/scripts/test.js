'use strict';

function onInit() {
    let stor = storage.get('hello')
    if(stor === null) {
        storage.set('hello', 1)
        stor = 1
    }

    createCommand('change', ['c'], (ctx) => {
        if(ctx.args.length > 0) {
            const i = parseInt(ctx.args[0])
            if(!isNaN(i)) {
                const before = storage.get('hello')
                storage.set('hello', i)
                ctx.sender.send(`Hello changed to ${before} to ${i}`)
            }
        } else {
            ctx.sender.send('nonex')
        }
    })

    createCommand('get', ['g'], (ctx) => {
        ctx.sender.send(`Hello is ${storage.get('hello')}`)
    })
    console.log(stor)
}