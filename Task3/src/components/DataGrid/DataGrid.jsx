import React from 'react';
import { DataGrid as DataGridMUI } from '@mui/x-data-grid';
import CircularProgress from '../CircularProgress';

function DataGrid({...props}) {
  return (
    <>
      <DataGridMUI
        {...props}
        components={{ LoadingOverlay: CircularProgress, NoErrorsOverlay: CircularProgress }}
      />
    </>
  );
}

export default DataGrid;
