import {SET_ATTENDANCE, SET_BEATS, SET_DEVICE_ID, SET_DEVICES, SET_PRESENCE, SET_QUOTA_EXCEEDED} from "./Actions";

export default (state, action) => {
    switch (action.type) {

        case SET_BEATS:
            return {
                ...state,
                beats: action.payload
            };
        case SET_PRESENCE:
            return {
                ...state,
                status: action.payload
            };
        case SET_ATTENDANCE:
            return {
                ...state,
                attendance: action.payload
            };
        case SET_DEVICE_ID:
            return {
                ...state,
                deviceId: action.payload
            }
        case SET_DEVICES:
            return {
                ...state,
                devices: action.payload
            }
        case SET_QUOTA_EXCEEDED:
            return {
                ...state,
                quotaExceeded: action.payload
            }
        default:
            return state;
    }
}