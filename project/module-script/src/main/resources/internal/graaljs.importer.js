// This file is used to get the imported classes or packages runtime.
// To import these, you should set the member named '_imports_' which is a GroupImports instance.
// Also you can use getImportedClass function in graaljs to get the class you want.
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
