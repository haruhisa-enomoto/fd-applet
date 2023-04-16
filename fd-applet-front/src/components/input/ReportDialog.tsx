import { useState } from "react";

import SendIcon from "@mui/icons-material/Send";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Grid,
  TextField,
  Typography,
} from "@mui/material";

type formData = {
  name: string;
  email: string;
  message: string;
};

const emptyData: formData = { name: "", email: "", message: "" };

const API_URL = "https://formsubmit.co/a0b5efd6257bf66bc34bd9b452205237";

export default function ReportDialog(prop: {
  open: boolean;
  setOpen: (value: boolean) => void;
}) {
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [formData, setFormData] = useState(emptyData);

  const handleClose = () => {
    setFormData(emptyData);
    prop.setOpen(false);
  };

  const handleFormChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [event.target.name]: event.target.value,
    });
  };

  const handleSubmit = () => {
    setIsSubmitted(true);
  };

  return (
    <div>
      <Dialog open={prop.open} onClose={handleClose}>
        <DialogTitle>Send feedback or report issues</DialogTitle>
        <form
          action={API_URL}
          method="POST"
          target="_blank"
          onSubmit={handleSubmit}
        >
          <DialogContent>
            <DialogContentText>
              Please send any bug reports or feature requests to the author.
              When you click the "Submit" button, you will be redirected to a
              site called "FormSubmit". Please follow the directions on that
              site to complete the submission process.
              <br />
              不具合や要望などありましたら気軽に送ってください。
              「Submit」を押すと英語のページに飛ばされるので、送信を完了するために指示に従ってください。
            </DialogContentText>
            <Grid container spacing={1} mt={1}>
              <Grid item xs={6}>
                <TextField
                  name="name"
                  label="Name"
                  value={formData.name}
                  onChange={handleFormChange}
                  fullWidth
                  required
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  name="email"
                  label="Email"
                  type="email"
                  value={formData.email}
                  onChange={handleFormChange}
                  fullWidth
                  required
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  name="message"
                  label="Message"
                  multiline
                  rows={4}
                  value={formData.message}
                  fullWidth
                  onChange={handleFormChange}
                  required
                />
              </Grid>
            </Grid>
          </DialogContent>
          {!isSubmitted ? (
            <DialogActions>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                startIcon={<SendIcon />}
              >
                Submit
              </Button>
              <Button
                onClick={() => {
                  prop.setOpen(false);
                }}
              >
                Cancel
              </Button>
            </DialogActions>
          ) : (
            <DialogActions sx={{ m: 1, gap: 2 }}>
              <Typography>Thank you for reporting!</Typography>
              <Button
                onClick={() => {
                  prop.setOpen(false);
                  setFormData(emptyData);
                  setIsSubmitted(false);
                }}
              >
                Close
              </Button>
            </DialogActions>
          )}
        </form>
      </Dialog>
    </div>
  );
}
