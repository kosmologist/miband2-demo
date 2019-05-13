import {createStore} from "redux";
import reducers from './Reducer'


const initialState = {
    beats : []
}

export const store = createStore(reducers,initialState)