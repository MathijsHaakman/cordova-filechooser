module.exports = {
    open: function (options, success, failure) {
        cordova.exec(success, failure, "FileChooser", "open", [options || {}]);
    }
};
