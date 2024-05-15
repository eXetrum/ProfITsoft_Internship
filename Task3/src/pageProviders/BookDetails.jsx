import BookDetailsPage from 'pages/book/details';
import React from 'react';

import PageContainer from './components/PageContainer';

const BookDetails = (props) => {
  return (
    <PageContainer>
      <BookDetailsPage {...props} />
    </PageContainer>
  );
};

export default BookDetails;
