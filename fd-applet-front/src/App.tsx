import { FC, useEffect, useState } from "react";

import EditIcon from "@mui/icons-material/Edit";
import { Alert, AlertTitle, Container, Link, Typography } from "@mui/material";
import MuiAppBar, { AppBarProps as MuiAppBarProps } from "@mui/material/AppBar";
import CssBaseline from "@mui/material/CssBaseline";
import Drawer from "@mui/material/Drawer";
import IconButton from "@mui/material/IconButton";
import { styled } from "@mui/material/styles";
import Toolbar from "@mui/material/Toolbar";

import packageJson from "../package.json";

import "./App.css";
import MyBackdrop from "./components/common/MyBackdrop";
import NotifyToast from "./components/common/NotifyToast";
import InputDrawer from "./components/input/InputDrawer";
import MyTabs from "./components/Tabs";
import { SelectionProvider } from "./contexts/SelectionContext";
import { useUuid } from "./contexts/UuidContext";
import useWindowDimensions from "./hooks/useWindowDimensions";
import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";

const version = packageJson.version;
// const version = "0.0.1";

const DRAWER_WIDTH_RATIO = 0.3;
const DRAWER_MAX_WIDTH = 350;

const Main = styled("main", {
  shouldForwardProp: (prop) => prop !== "open" && prop !== "drawerWidth",
})<{
  open?: boolean;
  drawerWidth?: number;
}>(({ theme, open, drawerWidth }) => ({
  flexGrow: 1,
  padding: 1,
  // width: "auto",
  transition: theme.transitions.create("margin", {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  marginLeft: `-${drawerWidth}px`,
  ...(open && {
    transition: theme.transitions.create("margin", {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    }),
    marginLeft: 0,
  }),
}));

interface AppBarProps extends MuiAppBarProps {
  open?: boolean;
  drawerWidth?: number;
}

const AppBar = styled(MuiAppBar, {
  shouldForwardProp: (prop) => prop !== "open" && prop !== "drawerWidth",
})<AppBarProps>(({ theme, open, drawerWidth }) => ({
  transition: theme.transitions.create(["margin", "width"], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  ...(open && {
    width: `calc(100% - ${drawerWidth}px)`,
    marginLeft: `${drawerWidth}px`,
    transition: theme.transitions.create(["margin", "width"], {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    }),
  }),
}));

const DrawerHeader = styled("div")(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  padding: theme.spacing(0, 1),
  // necessary for content to be below app bar
  ...theme.mixins.toolbar,
  justifyContent: "flex-end",
}));

const isLocalhost = () => {
  return (
    window.location.hostname === "localhost" ||
    window.location.hostname === "127.0.0.1"
  );
};

export type ComputationState = {
  [tabIndex: number]: boolean;
};

const App: FC = () => {
  const { windowWidth } = useWindowDimensions();
  const { uuid } = useUuid();

  const [openDrawer, setOpenDrawer] = useState(true);
  const [computationState, setComputationState] = useState<ComputationState>(
    {}
  );

  const drawerWidth = Math.min(
    windowWidth * DRAWER_WIDTH_RATIO,
    DRAWER_MAX_WIDTH
  );

  const [latestVersion, setLatestVersion] = useState<string | null>(null);
  const [isLocal, setIsLocal] = useState(false);

  useEffect(() => {
    fetch("https://haruhisa-enomoto.github.io/files/fd-applet-version.txt")
      .then((response) => response.text())
      .then((data) => {
        setLatestVersion(data.trim());
      })
      .catch((error) => {
        console.error("Error fetching latest version", error);
      });

    setIsLocal(isLocalhost());
  }, []);

  useEffect(() => {
    const handleBeforeUnload = () => {
      const data = new FormData();
      data.append("message", "WebUI will be closed!");

      const apiUrl = isLocal ? "/shutdown" : `/api/kill?client_id=${uuid}`; // For server hosting
      navigator.sendBeacon(apiUrl, data);
    };

    window.addEventListener("beforeunload", handleBeforeUnload);

    return () => {
      window.removeEventListener("beforeunload", handleBeforeUnload);
    };
  }, [uuid]);

  const needsUpdate = latestVersion && latestVersion !== version;

  const handleDrawerOpen = () => {
    setOpenDrawer(true);
  };

  const handleDrawerClose = () => {
    setOpenDrawer(false);
  };

  const handleComputationUpdate = (tabIndex: number) => (newState: boolean) => {
    return setComputationState((prevState) => ({
      ...prevState,
      [tabIndex]: newState,
    }));
  };

  const resetComputationState = () => {
    setComputationState({});
  };

  return (
    <Container maxWidth={false} sx={{ display: "flex" }}>
      <CssBaseline />
      <AppBar position="fixed" open={openDrawer} drawerWidth={drawerWidth}>
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            onClick={handleDrawerOpen}
            edge="start"
            sx={{ mr: 2, ...(openDrawer && { display: "none" }) }}
          >
            <EditIcon />
          </IconButton>
          <Typography variant="h6" noWrap component="div">
            FD Applet {version}
          </Typography>
        </Toolbar>
      </AppBar>
      <Drawer
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          "& .MuiDrawer-paper": {
            width: drawerWidth,
            boxSizing: "border-box",
            pl: 2,
            pr: 2,
          },
        }}
        variant="persistent"
        anchor="left"
        open={openDrawer}
      >
        <InputDrawer
          handleClose={handleDrawerClose}
          resetComputationState={resetComputationState}
        />
      </Drawer>
      <Main open={openDrawer} drawerWidth={drawerWidth}>
        <DrawerHeader />
        {needsUpdate && (
          <Alert severity="info">
            <AlertTitle>A new version of FD Applet is available!</AlertTitle>
            <Typography variant="body1">
              Current version: {version}, Latest version: {latestVersion}
            </Typography>
            <Typography variant="body1">
              Please download the latest version by the following link:
            </Typography>
            <Link
              variant="body1"
              target="_blank"
              rel="noopener"
              href="https://haruhisa-enomoto.github.io/fd-applet/"
            >
              https://haruhisa-enomoto.github.io/fd-applet/
            </Link>
          </Alert>
        )}
        {!isLocal && (
          <Alert severity="warning">
            <AlertTitle>Online Demo with Limitations</AlertTitle>
            <Typography variant="body1">
              This online demo has restricted computation memory and may run at
              a slower pace. Additionally, the connection could be unstable.
            </Typography>
            <Typography variant="body1">
              For a better experience, we recommend installing the local version
              using the following link:
            </Typography>
            <Link
              variant="body1"
              target="_blank"
              rel="noopener"
              href="https://haruhisa-enomoto.github.io/fd-applet/"
            >
              https://haruhisa-enomoto.github.io/fd-applet/
            </Link>
          </Alert>
        )}
        <SelectionProvider>
          <MyTabs
            computationState={computationState}
            handleComputationUpdate={handleComputationUpdate}
          />
        </SelectionProvider>
      </Main>
      <NotifyToast />
      <MyBackdrop />
    </Container>
  );
};

export default App;
