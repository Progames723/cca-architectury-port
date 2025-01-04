# cca-architectury-port
check out the original repo: https://github.com/Ladysnake/Cardinal-Components-API

idfk what to put in the license so i just used the same as the original

*i mightve just reinvented the wheel*

# USAGE
## Registration
just implement the interface of what ever component you wish to register
example:
```java
//example code do not use pls
public class InitComponents implements XComponentInitializer/*, YXComponentInitializer and etc */ {
    //yes the project is mojmap deal with it
    @Override
    public void registerXComponent(XComponentFactoryRegistry factory) {
		//register here
    }
}
```
if something is not mentioned here look at the original wiki 🤯

you also have to implement a `StaticComponentInitializer` to register your components(we cant have fabric/quilt entrypoints in multiplatform lol)