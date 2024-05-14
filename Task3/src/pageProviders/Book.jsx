import BookPage from 'pages/book';
import React from 'react';

import PageContainer from './components/PageContainer';

const Book = (props) => {
  return (
    <PageContainer>
      <BookPage {...props} />
    </PageContainer>
  );
};

export default Book;
