import { useIntl } from 'react-intl';
import React from 'react';
import Typography from 'components/Typography';

function BookList() {
  const { formatMessage } = useIntl();

  return (
    <Typography>
      {formatMessage({ id: 'title' })}
    </Typography>
  );
}

export default BookList;
