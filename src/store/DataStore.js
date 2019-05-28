import {createStore} from "redux";
import reducers from './Reducer'


const initialState = {
    beats : [],
    deviceId:'test'
}

export const store = createStore(reducers,initialState)