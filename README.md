
# react-native-command-executor

## Getting started

`$ npm install react-native-command-executor --save`

### Mostly automatic installation

`$ react-native link react-native-command-executor`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.imokhles.sncommandexecutor.SNCommandExecutorPackage;` to the imports at the top of the file
  - Add `new SNCommandExecutorPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-command-executor'
  	project(':react-native-command-executor').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-command-executor/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-command-executor')
  	```


## Usage
```javascript
import SNCommandExecutor from 'react-native-command-executor';

// Check Root status
SNCommandExecutor.verifyRootStatus((value) => {
    console.log('root status: '+value);
});

// execute command
let executed = SNCommandExecutor.executeCommand("which su");
console.log('Executed: '+ executed);

```
