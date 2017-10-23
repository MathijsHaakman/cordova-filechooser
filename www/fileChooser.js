module.exports = {
    open: function (success, failure, options) {
        cordova.exec(success, failure, "FileChooser", "open", [options || {}]);
    }
};
