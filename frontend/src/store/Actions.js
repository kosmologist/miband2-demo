export const SET_BEATS = 'SET_BEATS'

export function setBeats(beats) {

    return {
        type:SET_BEATS,
        payload: beats
    }

}