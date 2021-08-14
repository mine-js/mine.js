function onInit() {
    console.log('creating commands...')
    createCommand('test', ['t', 't2'], (ctx) => {
        //const random = jclass('java.util.Random').new(jlong(1))
        //console.log(`test: ${random.nextInt1(100)}`)
        console.log(`test: ${jclass('org.netherald.minejs.common.java.TestClass').staticGreeting1('Hello, Static World!')}`)
        console.log(ctx)
    })
    console.log('Hello World2!')
}