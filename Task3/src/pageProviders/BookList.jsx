import BookListPage from 'pages/book-list';
import React from 'react';

import PageContainer from './components/PageContainer';

const BookList = (props) => {
  return (
    <PageContainer>
      <BookListPage {...props} />
    </PageContainer>
  );
};

export default BookList;
