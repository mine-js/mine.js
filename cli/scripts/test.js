function onInit() {
    console.log('creating commands...')
    createCommand('test', ['t', 't2'], (ctx) => {
        const random = jclass('java.util.Random').new()
        console.log(`test: ${random.nextInt1(100)}`)
        console.log(ctx)
    })
    console.log('Hello World2!')
}