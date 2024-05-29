import { applyMiddleware, createStore } from 'redux';
import { thunk } from 'redux-thunk';
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/lib/storage'

import rootReducer from '../reducers/authors';

const configureStore = () => { 
  const persistConfig = {
    key: 'root',
    storage,
    whitelist: ['currentPage', 'pageSize']
  };
  const persistedReducer = persistReducer(persistConfig, rootReducer);
  const store = createStore(persistedReducer, applyMiddleware(thunk));
  const persistor = persistStore(store);
  return { store, persistor };
};

export default configureStore; 