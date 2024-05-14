import React, { useMemo } from 'react';
import IntlProvider from 'misc/providers/IntlProvider';
import useLocationSearch from 'misc/hooks/useLocationSearch';

import getMessages from './intl';
import AuthorList from './containers/AuthorList';

import { applyMiddleware, createStore } from 'redux';
import { Provider } from 'react-redux';
import { thunk } from 'redux-thunk';
import rootReducer from './reducers/authors';

const store = createStore(rootReducer, applyMiddleware(thunk));

function Index(props) {
  const {
    lang,
  } = useLocationSearch();
  const messages = useMemo(() => getMessages(lang), [lang]);
  return (
    <Provider store={store}>
      <IntlProvider messages={messages}>
        <AuthorList {...props} />
      </IntlProvider>
    </Provider>
  );
}

export default Index;
