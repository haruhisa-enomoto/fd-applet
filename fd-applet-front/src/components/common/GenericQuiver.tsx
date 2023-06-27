import { useEffect, useRef, useState } from "react";

import VisGraph, {
  GraphData,
  GraphEvents,
  Network,
  Node,
} from "react-vis-graph-wrapper";

import BlockIcon from "@mui/icons-material/Block";
import FitScreenIcon from "@mui/icons-material/FitScreen";
import LabelIcon from "@mui/icons-material/Label";
import LabelOffIcon from "@mui/icons-material/LabelOff";
import PauseCircleIcon from "@mui/icons-material/PauseCircle";
import PlayCircleIcon from "@mui/icons-material/PlayCircle";
import SearchIcon from "@mui/icons-material/Search";
import {
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton,
  Paper,
  Stack,
  Typography,
} from "@mui/material";

import useFetchWithUiFeedback from "../../hooks/useFetchWithUiFeedback";
import useWindowDimensions from "../../hooks/useWindowDimensions";
import { SlimQuiverData, fatten } from "../../types/QuiverTypes";

import ComputeButton from "./ComputeButton";
import LargerTooltip from "./LargerTooltip";
import { defaultOptions } from "./QuiverOptions";
import UpdateButton from "./UpdateButton";

const defaultData: GraphData = {
  nodes: [],
  edges: [],
};


function changeGroup(node: Node, groupName: string): Node {
  return {
    ...node,
    group: groupName,
  };
}

function isEqual(list1: unknown[], list2: unknown[]): boolean {
  return (
    list1.length === list2.length &&
    list1.every((val, index) => val === list2[index])
  );
}

interface GenericQuiverProps {
  url: string;
  selected?: string[];
  secondarySelected?: string[];
  highlighted?: string[];
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  physicOption?: any;
  height?: number;
  updateButton?: boolean;
  allowChosen?: boolean;
  events?: GraphEvents;
  // showNumber?: boolean;
  buttonTitle?: string;
  hide?: boolean;
  computationCallback?: () => void;
  showDuration?: boolean;
}

export default function GenericQuiver({
  url,
  highlighted = [],
  selected = [],
  secondarySelected = [],
  physicOption,
  height,
  updateButton = false,
  allowChosen = false,
  buttonTitle,
  events,
  hide = false,
  showDuration = false,
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  computationCallback = () => { },
}: GenericQuiverProps) {
  const { windowHeight } = useWindowDimensions();
  const fetchWithUiFeedback = useFetchWithUiFeedback();

  const [nodes, setNodes] = useState(defaultData.nodes);
  const [edges, setEdges] = useState(defaultData.edges);
  const [physics, setPhysics] = useState(true);
  const [showLabel, setShowLabel] = useState(true);
  const [zoomView, setZoomView] = useState(false);
  const [networkInstance, setNetworkInstance] = useState({});

  const [computed, setComputed] = useState(false);

  const [openAlert, setOpenAlert] = useState(false);
  const [numVertices, setNumVertices] = useState(0);
  const [tempQuiverData, setTempQuiverData] = useState<SlimQuiverData>();

  const prevSelected = useRef(selected);
  const prevSecondarySelected = useRef(secondarySelected);
  const prevHighlighted = useRef(highlighted);

  const handleZoomView = (event: React.ChangeEvent<HTMLInputElement>) => {
    setZoomView(event.target.checked);
  };

  const handlePhysics = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPhysics(event.target.checked);
  };

  const handleLabel = (event: React.ChangeEvent<HTMLInputElement>) => {
    setShowLabel(event.target.checked);
  };

  async function getQuiver() {
    const response = await fetchWithUiFeedback<SlimQuiverData>({
      url: url,
      showDuration: showDuration,
    });
    if (response.data === undefined) return;

    const numNodes = response.data.vertices.length;
    setNumVertices(numNodes);
    if (numNodes > 200) {
      setOpenAlert(true);
      console.log(numNodes);
      setTempQuiverData(response.data);
      return;
    }
    setQuiver(response.data);
  }

  const setQuiver = (slimData: SlimQuiverData) => {
    const fattenData = fatten(slimData);
    setNodes(fattenData.nodes);
    setEdges(fattenData.edges);
    setComputed(true);
    computationCallback();
  };

  const handleAlertClose = (showBigQuiver: boolean) => {
    setOpenAlert(false);
    if (showBigQuiver && tempQuiverData) {
      setQuiver(tempQuiverData);
    }
  };

  useEffect(() => {
    if (
      !isEqual(selected, prevSelected.current) ||
      !isEqual(secondarySelected, prevSecondarySelected.current) ||
      !isEqual(highlighted, prevHighlighted.current)
    ) {
      setNodes((nodes) =>
        nodes.map((node) => {
          if (highlighted.includes(node.title as string)) {
            return changeGroup(node, "highlight");
          } else if (
            selected.includes(node.title as string) &&
            secondarySelected.includes(node.title as string)
          ) {
            return changeGroup(node, "groupBoth");
          } else if (selected.includes(node.title as string)) {
            return changeGroup(node, "groupOne");
          } else if (secondarySelected.includes(node.title as string)) {
            return changeGroup(node, "groupTwo");
          } else {
            return changeGroup(node, "default");
          }
        })
      );
      prevSelected.current = selected;
      prevSecondarySelected.current = secondarySelected;
      prevHighlighted.current = highlighted;
    }
  }, [selected, secondarySelected, highlighted]);

  return (
    <>
      {!computed || hide ? (
        <ComputeButton
          onClick={getQuiver}
          iconButton={false}
          title={buttonTitle ? buttonTitle : "Compute quiver"}
        />
      ) : (
        <>
          {updateButton && <UpdateButton onClick={getQuiver} title="Update" />}
          <Typography m={1}>
            {nodes.length} vertices and {edges.length} arrows
          </Typography>
          <Paper
            sx={{
              width: "auto",
              height: height !== undefined ? height : windowHeight - 200,
              border: 1,
              borderRadius: 1,
              borderColor: "lightgray",
              display: "flex",
              m: 1,
            }}
          >
            <VisGraph
              graph={{ nodes: nodes, edges: edges }}
              options={{
                ...defaultOptions,
                nodes: {
                  ...defaultOptions.nodes,
                  chosen: allowChosen,
                  physics: physics,
                  ...(showLabel
                    ? {
                      shape: "ellipse",
                      font: {
                        face: "roboto",
                        size: 30,
                      },
                    }
                    : {
                      shape: "dot",
                      font: {
                        size: 0,
                      },
                      size: 25,
                    }),
                },
                physics:
                  physicOption === undefined
                    ? defaultOptions.physics
                    : physicOption,
                interaction: {
                  zoomView: zoomView,
                },
              }}
              getNetwork={setNetworkInstance}
              events={events}
            />
          </Paper>
          <Stack direction="row" justifyContent={"space-around"}>
            <LargerTooltip title="Fit screen">
              <IconButton
                onClick={() => {
                  (networkInstance as Network).fit();
                }}
              >
                <FitScreenIcon />
              </IconButton>
            </LargerTooltip>
            <LargerTooltip
              title={
                zoomView ? "Disable scroll to zoom" : "Enable scroll to zoom"
              }
            >
              <Checkbox
                icon={<SearchIcon color="action" />}
                checked={zoomView}
                onChange={handleZoomView}
                checkedIcon={<BlockIcon color="action" />}
              />
            </LargerTooltip>
            <LargerTooltip
              title={
                physics
                  ? "Disable physics simulation"
                  : "Enable physics simulation"
              }
            >
              <Checkbox
                icon={<PlayCircleIcon color="action" />}
                checked={physics}
                onChange={handlePhysics}
                checkedIcon={<PauseCircleIcon color="action" />}
              />
            </LargerTooltip>
            <LargerTooltip
              title={showLabel ? "Hide vertex labels" : "Show vertex labels"}
            >
              <Checkbox
                icon={<LabelIcon />}
                checked={showLabel}
                onChange={handleLabel}
                checkedIcon={<LabelOffIcon color="action" />}
              />
            </LargerTooltip>
          </Stack>
        </>
      )}
      <Dialog open={openAlert} onClose={() => handleAlertClose(false)}>
        <DialogTitle>{`There are ${numVertices} vertices!`}</DialogTitle>
        <DialogContent>
          <DialogContentText>
            There are more than 200 vertices, so displaying them may be slow and
            not visually interesting. Are you sure you want to display the
            quiver?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => handleAlertClose(true)}>Show</Button>
          <Button onClick={() => handleAlertClose(false)} autoFocus>
            Cancel
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
