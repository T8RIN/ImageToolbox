package ru.tech.imageresizershrinker.utils.permission

class PermissionResult {
    var permissionStatus: HashMap<String, PermissionStatus> = hashMapOf()
    var finalStatus: PermissionStatus = PermissionStatus.NOT_GIVEN
}