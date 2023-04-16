import { useState } from "react";

import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  TextField,
} from "@mui/material";

interface NakayamaDialogProps {
  open: boolean;
  setOpen: (value: boolean) => void;
  handleCloseWithString: (value: string) => void;
}

export default function NakayamaDialog(prop: NakayamaDialogProps) {
  const [inputValue, setInputValue] = useState("");

  const handleClose = () => {
    prop.handleCloseWithString(inputValue);
    prop.setOpen(false);
  };

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(event.target.value);
  };

  return (
    <Dialog open={prop.open} onClose={handleClose}>
      <DialogTitle>Nakayama algebra from Kupisch series</DialogTitle>
      <DialogContent>
        <DialogContentText>
          Enter a Kupisch series of a Nakayama algebra:
          <br />
          A quiver will be like 1→2→3→…, and enter series dim P(1), dim P(2), ….
          <br />
          For example, "3, 2, 1" (linearly oriented A3),
          <br /> or "5, 5, 6, 5" (rank 4 cyclic Nakayama).
        </DialogContentText>
        <TextField
          autoFocus
          margin="dense"
          fullWidth
          variant="standard"
          value={inputValue}
          onChange={handleInputChange}
        />
      </DialogContent>
      <DialogActions>
        <Button variant="contained" onClick={handleClose}>
          OK
        </Button>
        <Button
          variant="outlined"
          onClick={() => {
            prop.setOpen(false);
          }}
        >
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  );
}
