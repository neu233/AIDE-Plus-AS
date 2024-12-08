

package io.github.zeroaicy.aide.aaptcompiler.permissions

/**
 * Model for a permission group entry.
 *
 * @property constant The constant value of the permission group.
 * @author Akash Yadav
 */
enum class PermissionGroup(val constant: String) {
    ACTIVITY_RECOGNITION(ManifestPermissionConstants.GROUP_ACTIVITY_RECOGNITION),
    CALENDAR(ManifestPermissionConstants.GROUP_CALENDAR),
    CALL_LOG(ManifestPermissionConstants.GROUP_CALL_LOG),
    CAMERA(ManifestPermissionConstants.GROUP_CAMERA),
    CONTACTS(ManifestPermissionConstants.GROUP_CONTACTS),
    LOCATION(ManifestPermissionConstants.GROUP_LOCATION),
    MICROPHONE(ManifestPermissionConstants.GROUP_MICROPHONE),
    NEARBY_DEVICES(ManifestPermissionConstants.GROUP_NEARBY_DEVICES),
    NOTIFICATIONS(ManifestPermissionConstants.GROUP_NOTIFICATIONS),
    PHONE(ManifestPermissionConstants.GROUP_PHONE),
    READ_MEDIA_AURAL(ManifestPermissionConstants.GROUP_READ_MEDIA_AURAL),
    READ_MEDIA_VISUAL(ManifestPermissionConstants.GROUP_READ_MEDIA_VISUAL),
    SENSORS(ManifestPermissionConstants.GROUP_SENSORS),
    SMS(ManifestPermissionConstants.GROUP_SMS),
    STORAGE(ManifestPermissionConstants.GROUP_STORAGE)
}