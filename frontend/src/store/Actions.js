export const SET_BEATS = 'SET_BEATS'
export const SET_PRESENCE = 'SET_PRESENCE'

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