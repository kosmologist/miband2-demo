import {SET_BEATS} from "./Actions";

export default (state, action) => {
    switch (action.type) {

        case SET_BEATS:
            return {
                ...state,
                beats: action.payload
            }
        default:
            return state;
    }
}