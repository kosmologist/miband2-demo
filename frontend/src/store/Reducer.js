import {SET_ATTENDANCE, SET_BEATS, SET_PRESENCE} from "./Actions";

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
        default:
            return state;
    }
}