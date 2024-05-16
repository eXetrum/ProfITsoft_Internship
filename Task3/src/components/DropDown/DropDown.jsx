import * as React from 'react';
import Box from '@mui/material/Box';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';

export default function DropDown({ onChange, value, label, title, items, ...props}) {
  return (
    <Box sx={{ minWidth: 120 }}>
      <FormControl fullWidth>
        <InputLabel id="dropdown-select-label">{title}</InputLabel>
        <Select
          labelId="dropdown-select-label"
          id="dropdown-select"
          value={value}
          label={label}
          onChange={onChange}
        >
          {items.map(entry => (<MenuItem key={entry.id} value={entry.id}>{entry.name}</MenuItem>))}
        </Select>
      </FormControl>
    </Box>
  );
}