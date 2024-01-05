package ru.tech.imageresizershrinker.core.ui.utils.permission

class PermissionResult {
    var permissionStatus: HashMap<String, PermissionStatus> = hashMapOf()
    var finalStatus: PermissionStatus = PermissionStatus.NOT_GIVEN
}