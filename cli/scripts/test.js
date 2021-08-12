function onInit() {
    console.log('creating commands...')
    const random = jclass('java.util.Random').new()
    console.log('Random: ' + random.nextInt(100))
    createCommand('test', ['t', 't2'], (ctx) => {
        console.log(ctx)
    })
    console.log('Hello World2!')
}