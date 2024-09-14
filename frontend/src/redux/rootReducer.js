import { combineReducers } from 'redux';
import socketReducer from './socketReducer';
// Importar outros reducers

const rootReducer = combineReducers({
    socket: socketReducer,
    // Outros reducers
});

export default rootReducer;