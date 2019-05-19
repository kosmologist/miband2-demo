export const SET_BEATS = 'SET_BEATS'
export const SET_PRESENCE = 'SET_PRESENCE'
export const SET_ATTENDANCE = 'SET_ATTENDANCE'
export const SET_DEVICE_ID = 'SET_DEVICE_ID'

export function setBeats(beats) {
    return {
        type:SET_BEATS,
        payload: beats
    }
}

export function setPresence(status) {
    return {
        type: SET_PRESENCE,
        payload: status
    }
}

export function setAttendance(attendance) {
    return {
        type:SET_ATTENDANCE,
        payload:attendance
    }
}

export function setDeviceId(deviceId) {
    return {
        type: SET_DEVICE_ID,
        payload:deviceId
    }
}