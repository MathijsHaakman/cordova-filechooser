Cordova FileChooser Plugin
======

# Requirements

Requires Cordova >= 2.8.0

# Installation
Install with Cordova CLI

	$ cordova plugin add https://github.com/MathijsHaakman/cordova-filechooser.git

Install with Plugman

	$ plugman --platform android --project /path/to/project \
		--plugin https://github.com/MathijsHaakman/cordova-filechooser.git


# API reference

## fileChooser.open
Opens a filebrowser to choose a file

### Parameters
- __successCallback__: (Optional) The callback that is called when a file is chosen.
- __errorCallback__: (Optional) The callback that is called when something errors.
- __options__: (Optional) An object with additional parameters.

### Example
This example opens the fileChooser where you can only choose .pdf files.
```
filleChooser.open(
  function (uri) {
    alert(uri);
  },
  function (error) {
    alert(error);
  },
  {
    mimeTypes: ["application/pdf"]
});
```


## successCallback
Function which is called when a file is chosen.

### Parameters
- __uri__: The URI of the chosen file.


## errorCallback
Function which is called in case something fails

### Parameters
- __error__: Error in case something fails.


## options
Optional parameters to customize the files to choose.

### Properties
| Name | Type | Default | Description |
| --- | --- | --- | --- |
| mimeTypes | array | [] | An array of strings of MIME types. Filling this array with MIME types limits the fileChooser to choose files with only these MIME types. |


# Screenshot

![Screenshot](filechooser.png "Screenshot")
