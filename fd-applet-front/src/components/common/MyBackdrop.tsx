import { useEffect, useState } from "react";

import Backdrop from "@mui/material/Backdrop";
import CircularProgress from "@mui/material/CircularProgress";

import { useUi } from "../../contexts/UiContext";

export default function MyBackdrop() {
  const { openBack } = useUi();
  const [showBackdrop, setShowBackdrop] = useState(false);

  useEffect(() => {
    // Wait a little to actually show backdrop
    let timeoutId: NodeJS.Timeout;

    if (openBack) {
      timeoutId = setTimeout(() => {
        setShowBackdrop(true);
      }, 500);
    } else {
      setShowBackdrop(false);
    }

    return () => {
      clearTimeout(timeoutId);
    };
  }, [openBack]);

  return (
    <Backdrop
      sx={{ color: "#fff", zIndex: (theme) => theme.zIndex.drawer + 1 }}
      open={showBackdrop}
    >
      <CircularProgress color="inherit" />
    </Backdrop>
  );
}
