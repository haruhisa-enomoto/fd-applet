import * as React from "react";

import MuiAlert, { AlertProps } from "@mui/material/Alert";
import Snackbar from "@mui/material/Snackbar";

import { useUi } from "../../contexts/UiContext";

const Alert = React.forwardRef<HTMLDivElement, AlertProps>(function Alert(
  props,
  ref
) {
  return <MuiAlert elevation={6} ref={ref} variant="standard" {...props} />;
});

export default function NotifyToast() {
  const { notifyStatus, openNotify, setOpenNotify } = useUi();

  const handleClose = () => {
    setOpenNotify(false);
  };

  return (
    <Snackbar
      open={openNotify}
      autoHideDuration={notifyStatus.duration}
      onClose={handleClose}
      anchorOrigin={{ horizontal: "center", vertical: "top" }}
    >
      <Alert
        onClose={handleClose}
        severity={notifyStatus.severity}
        sx={{ width: "100%" }}
      >
        {notifyStatus.message}
      </Alert>
    </Snackbar>
  );
}
