import React, { useMemo } from 'react';
import IntlProvider from 'misc/providers/IntlProvider';
import useLocationSearch from 'misc/hooks/useLocationSearch';
import { Provider } from 'react-redux';
import { PersistGate } from 'redux-persist/integration/react'
import getMessages from './containers/intl';
import BookDetails from './containers/BookDetails';

import configureStore from '../redux';

const { store, persistor } = configureStore();

function Index(props) {
  const {
    lang,
  } = useLocationSearch();
  
  const messages = useMemo(() => getMessages(lang), [lang]);
  return (
    <Provider store={store}>
      <PersistGate loading={null} persistor={persistor}>
        <IntlProvider messages={messages}>
          <BookDetails {...props} />
        </IntlProvider>
      </PersistGate>
    </Provider>
  );
}

export default Index;
