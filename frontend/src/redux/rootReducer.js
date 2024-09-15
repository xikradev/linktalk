import { combineReducers } from 'redux';
import userReducer from './userReducer';
// Importar outros reducers

const rootReducer = combineReducers({
    user: userReducer
    // Outros reducers
});

export default rootReducer;