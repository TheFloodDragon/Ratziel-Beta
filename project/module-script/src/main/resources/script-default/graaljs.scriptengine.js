// avoid unnecessary chaining of __noSuchProperty__ again
// in case user loads this script more than once.
if (typeof getImportedClass == 'undefined' || !(getImportedClass instanceof Function)) {

    Object.defineProperty(this, "getImportedClass", {
        configurable: true, enumerable: false, writable: true, value: (function () {
            var global = this;
            var oldNoSuchProperty = global.__noSuchProperty__;
            var __noSuchProperty__ = function (name) {
                'use strict';
                if ("_imports_" in global) {
                    var clazz = _imports_.lookupClass(name);
                    if (clazz) {
                        var jsClass = Java.type(clazz.getName());
                        global[clazz.getSimpleName()] = jsClass;
                        return jsClass;
                    }
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
                writable: true, configurable: true, enumerable: false, value: __noSuchProperty__
            });

            return function () {
                if ("_imports_" in global) {
                    var clazz = _imports_.lookupClass(arguments[0]);
                    if (clazz) {
                        return Java.type(clazz.name);
                    }
                }
                return undefined
            }
        })()
    });
}
