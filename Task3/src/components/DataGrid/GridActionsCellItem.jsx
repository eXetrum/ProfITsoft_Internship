import React from 'react';
import { GridActionsCellItem as GridActionsCellItemMUI } from '@mui/x-data-grid';

function GridActionsCellItem({ ...props }) {
  return (
      <GridActionsCellItemMUI
        {...props}
      />
  );
}

export default GridActionsCellItem;
