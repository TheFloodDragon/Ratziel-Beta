// avoid unnecessary chaining of __noSuchProperty__ again
// in case user loads this script more than once.
if (typeof getImportedClass == 'undefined' || !(getImportedClass instanceof Function)) {

    Object.defineProperty(this, "getImportedClass", {
        configurable: true, enumerable: false, writable: true,
        value: (function () {
            var global = this;
            var oldNoSuchProperty = global.__noSuchProperty__;
            var ScriptImporter = Java.type('cn.fd.ratziel.module.script.ScriptManager.Importer');
            var __noSuchProperty__ = function (name) {
                'use strict';
                var clazz = ScriptImporter.getImportedClass(name);
                if (clazz) {
                    return Java.type(clazz.name);
                }
                if (oldNoSuchProperty) {
                    return oldNoSuchProperty.call(this, name);
                } else {
                    if (this === undefined) {
                        throw new ReferenceError(name + " is not defined");
                    } else {
                        return undefined;
                    }
                }
            }

            Object.defineProperty(global, "__noSuchProperty__", {
                writable: true, configurable: true, enumerable: false,
                value: __noSuchProperty__
            });

            return function () {
                var clazz = ScriptImporter.getImportedClass(name);
                if (clazz) {
                    return Java.type(clazz.name);
                }
            }
        })()
    });
}
