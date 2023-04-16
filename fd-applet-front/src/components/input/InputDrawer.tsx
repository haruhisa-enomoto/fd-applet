import { useRef, useState } from "react";

import VisGraph, {
  Edge,
  GraphData,
  Network,
  Options,
} from "react-vis-graph-wrapper";

import { saveAs } from "file-saver";

import BackspaceIcon from "@mui/icons-material/Backspace";
import BlockIcon from "@mui/icons-material/Block";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import DeleteIcon from "@mui/icons-material/Delete";
import FitScreenIcon from "@mui/icons-material/FitScreen";
import PauseCircleIcon from "@mui/icons-material/PauseCircle";
import PlayCircleIcon from "@mui/icons-material/PlayCircle";
import SearchIcon from "@mui/icons-material/Search";
import SyncIcon from "@mui/icons-material/Sync";
import {
  Box,
  Button,
  Checkbox,
  IconButton,
  Paper,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { grey } from "@mui/material/colors";
import Divider from "@mui/material/Divider";
import { styled } from "@mui/material/styles";

import { useUi } from "../../contexts/UiContext";
import useFetchWithUiFeedback from "../../hooks/useFetchWithUiFeedback";
import {
  fatten,
  relationDataToString,
  slim,
  SlimAlgebraData,
  stringToRelationData,
} from "../../types/QuiverTypes";
import LargerTooltip from "../common/LargerTooltip";

import FileMenu from "./FileMenu";
import NakayamaDialog from "./NakayamaDialog";
import ReportDialog from "./ReportDialog";

const defaultData: GraphData = {
  nodes: [
    { id: "1", label: "1", title: "1" },
    { id: "2", label: "2", title: "2" },
    { id: "3", label: "3", title: "3" },
  ],
  edges: [
    { label: "a", from: "1", to: "2" },
    { label: "b", from: "2", to: "3" },
  ],
};

const defaultOptions: Options = {
  nodes: {
    physics: false,
    font: {
      face: "roboto",
      size: 20,
    },
    color: {
      background: grey[100],
      border: grey[600],
      highlight: {
        background: grey[200],
        border: grey[600],
      },
    },
  },
  edges: {
    arrows: "to",
    color: {
      inherit: true,
    },
    font: {
      face: "roboto",
      size: 20,
    },
    smooth: {
      enabled: true,
      type: "dynamic",
      roundness: 0.5,
    },
  },
  interaction: {
    zoomView: false,
  },
};

function nextNumber(listOfNumbers: (string | undefined)[]) {
  // const nodeNames = Object.values(algNodes.get()).map((obj) => obj.label);
  let i = 1;
  while (listOfNumbers.includes(String(i))) {
    i++;
  }
  return String(i);
}

function nextAlphabet(listOfLetters: (string | undefined)[]): string {
  const alphabets = "abcdefghijklmnopqrstuvwxyz";
  let i = 0;
  let candidate = alphabets[i % 26];

  while (listOfLetters.includes(candidate)) {
    i++;
    candidate = alphabets[i % 26] + (Math.floor(i / 26) || "");
  }

  return candidate;
}

const DrawerHeader = styled("div")(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  padding: theme.spacing(0, 1),
  ...theme.mixins.toolbar,
  justifyContent: "space-between",
}));

interface InputDrawerProps {
  handleClose: () => void;
  resetComputationState: () => void;
}

type loopNumberDict = {
  [vertex: string]: number;
};

export default function InputDrawer({
  handleClose,
  resetComputationState,
}: InputDrawerProps) {
  const { setNotifyStatus, setOpenNotify } = useUi();
  const fetchWithUiFeedback = useFetchWithUiFeedback();

  const [nodes, setNodes] = useState(defaultData.nodes);
  const [edges, setEdges] = useState(defaultData.edges);
  const [physics, setPhysics] = useState(true);
  const [zoomView, setZoomView] = useState(false);

  const [source, setSource] = useState<string | null>(null);
  const [loopNumberDict, setLoopNumberDict] = useState<loopNumberDict>({});

  const [networkInstance, setNetworkInstance] = useState({});

  const [inputValue, setInputValue] = useState("");
  const fileInput = useRef<HTMLInputElement>(null);
  const [fileName, setFileName] = useState<string>("my-algebra");

  const [openNakayama, setOpenNakayama] = useState(false);
  const [openReport, setOpenReport] = useState(false);

  async function postAlgebraData() {
    const relationData = stringToRelationData(inputValue);
    const currentData: SlimAlgebraData = {
      quiver: slim({ nodes: nodes, edges: edges }),
      monoRelations: relationData.monoRelations,
      biRelations: relationData.biRelations,
    };
    const response = await fetchWithUiFeedback<SlimAlgebraData>({
      url: `/api/algebra`,
      method: "POST",
      body: currentData,
    });
    if (response.data === undefined) return;
    setInputValue(
      relationDataToString({
        monoRelations: response.data.monoRelations,
        biRelations: response.data.biRelations,
      })
    );
    resetComputationState();
  }

  const handlePhysics = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPhysics(event.target.checked);
  };

  const handleZoomView = (event: React.ChangeEvent<HTMLInputElement>) => {
    setZoomView(event.target.checked);
  };

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(event.target.value);
  };

  const addEdge = (
    from: string | number,
    to: string | number,
    loopSize?: number
  ) => {
    const newLetter = nextAlphabet(edges.map((obj) => obj.label));
    setEdges([
      ...edges,
      {
        id: newLetter,
        label: newLetter,
        from,
        to,
        ...(loopSize !== undefined
          ? { selfReference: { size: loopSize } }
          : {}),
      },
    ]);
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleClick = (params: any) => {
    // Check if a node is clicked or an empty space is clicked
    if (params.nodes.length > 0) {
      if (source === null) {
        // New vertex is clicked and set this as source vertex.
        setSource(params.nodes[0]);
      } else {
        // We already have a source, so add an edge to the clicked target vertex.
        const target = params.nodes[0];
        let loopSize;

        if (source === target) {
          // If the source and target are the same, it's a loop.
          const currentLoopNumber = loopNumberDict[source] || 0;
          loopSize = 20 * (currentLoopNumber + 1);

          setLoopNumberDict({
            ...loopNumberDict,
            [source]: currentLoopNumber + 1,
          });
        }

        addEdge(source, target, loopSize);
        (networkInstance as Network).unselectAll();
        setSource(null);
      }
    } else if (params.nodes.length === 0 && params.edges.length === 0) {
      // Clicked on an empty space, add a new vertex.
      const nextNum = nextNumber(nodes.map((obj) => obj.label));
      setNodes([
        ...nodes,
        {
          id: nextNum,
          label: nextNum,
          x: params.pointer.canvas.x,
          y: params.pointer.canvas.y,
        },
      ]);

      if (source !== null) {
        // If we have a source, add an edge to the new vertex.
        addEdge(source, nextNum);
        setSource(null);
      }
    }
  };

  const handleImport = () => {
    if (fileInput.current) {
      fileInput.current.value = "";
      fileInput.current.click();
    }
  };

  const algOpen = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const fileContent = e.target?.result;
        if (typeof fileContent === "string") {
          try {
            const data: SlimAlgebraData = JSON.parse(fileContent);
            const fatData = fatten(data.quiver);
            setNodes(fatData.nodes);
            setEdges(fatData.edges);
            setInputValue(
              relationDataToString({
                monoRelations: data.monoRelations,
                biRelations: data.biRelations,
              })
            );
            setFileName(file.name);
          } catch (error) {
            setNotifyStatus({
              message: "Invalid file.",
              severity: "error",
            });
            setOpenNotify(true);
            console.log(error);
          }
        }
      };
      reader.readAsText(file);
      setSource(null);
    }
  };

  const algSave = () => {
    // Convert the data to a JSON string
    const relationData = stringToRelationData(inputValue);
    const currentData: SlimAlgebraData = {
      quiver: slim({ nodes: nodes, edges: edges }),
      monoRelations: relationData.monoRelations,
      biRelations: relationData.biRelations,
    };
    const jsonData = JSON.stringify(currentData);
    // Create a Blob object containing the data
    const blob = new Blob([jsonData], { type: "application/json" });

    // Use the `saveAs` function to save the file
    saveAs(blob, fileName);
  };

  const handleClear = () => {
    setEdges([]);
    setNodes([]);
    setInputValue("");
    setSource(null);
  };

  const handleFit = () => {
    (networkInstance as Network).fit();
    setSource(null);
  };

  const handleDelete = () => {
    const test = (networkInstance as Network).getSelection();
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    setNodes(nodes.filter((node) => !test.nodes.includes(node.id!)));
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    setEdges(edges.filter((edge) => !test.edges.includes(edge.id!)));
    setSource(null);
  };

  const handleKey = (event: React.KeyboardEvent) => {
    if (event.key === "Delete" || event.key === "Backspace") {
      handleDelete();
    }
  };

  const nakayamaClose = (value: string) => {
    setOpenNakayama(true);
    const parsed: number[] = value.split(",").map((num) => parseInt(num));
    if (parsed.includes(NaN)) {
      setNotifyStatus({
        message: "Invalid input.",
        severity: "error",
      });
      setOpenNotify(true);
      return;
    }
    const N = parsed.length;
    const isLinear: boolean = parsed[N - 1] === 1;
    const numbers: number[] = []; // [1, 2, 3, ..., N]
    for (let i = 1; i <= N; i++) {
      numbers.push(i);
    }
    for (let i = 1; i <= N; i++) {
      setNodes(
        numbers.map((num) => ({
          id: String(num),
          label: String(num),
        }))
      );
    }
    const newEdges: Edge[] = [];
    const usedLabels: string[] = [];
    for (let i = 1; i <= N; i++) {
      if (isLinear && i === N) break;
      const next = i === N ? 1 : i + 1;
      const newLetter = nextAlphabet(usedLabels);
      newEdges.push({
        label: newLetter,
        from: String(i),
        to: String(next),
      });
      usedLabels.push(newLetter);
    }
    setEdges(newEdges);
    const relations: string[][] = [];
    for (let i = 1; i <= N; i++) {
      const rel: string[] = [];
      if (isLinear && i + parsed[i - 1] > N) continue;
      for (let length = 0; length < parsed[i - 1]; length++) {
        rel.push(usedLabels[(i - 1 + length) % N]);
      }
      relations.push(rel);
    }
    setInputValue(relations.map((rel) => rel.join("*")).join(", "));
  };

  return (
    <>
      <DrawerHeader>
        <FileMenu
          save={algSave}
          open={handleImport}
          handleNakayama={() => {
            setOpenNakayama(true);
          }}
          report={() => {
            setOpenReport(true);
          }}
        />
        <Button endIcon={<ChevronLeftIcon />} onClick={handleClose}>
          Hide
        </Button>
      </DrawerHeader>
      <Divider />
      <Typography variant="subtitle1" m={1}>
        Click to add. Use delete key to erase.
      </Typography>
      <Box height={300}>
        <Paper
          sx={{
            width: "auto",
            height: 300,
            border: 1,
            borderRadius: 1,
            borderColor: "lightgray",
          }}
        >
          <VisGraph
            graph={{ nodes: nodes, edges: edges }}
            options={{
              ...defaultOptions,
              nodes: { ...defaultOptions.nodes, physics: physics },
              interaction: {
                zoomView: zoomView,
              },
            }}
            events={{ click: handleClick }}
            getNetwork={setNetworkInstance}
            onKeyUp={handleKey}
          />
        </Paper>
      </Box>
      <Stack direction="row" justifyContent="space-around" mt={1}>
        <LargerTooltip title="Fit screen">
          <IconButton onClick={handleFit}>
            <FitScreenIcon />
          </IconButton>
        </LargerTooltip>
        <LargerTooltip title="Delete selected item">
          <IconButton onClick={handleDelete}>
            <BackspaceIcon />
          </IconButton>
        </LargerTooltip>
        <LargerTooltip title="Clear all">
          <IconButton onClick={handleClear}>
            <DeleteIcon />
          </IconButton>
        </LargerTooltip>
      </Stack>
      <Box sx={{ width: "100%", spacing: 1, p: 1 }}>
        <TextField
          multiline
          label="Relations (example: ab, c * d - e fg, hhh )"
          onChange={handleInputChange}
          fullWidth
          size="small"
          value={inputValue}
        />
        <input
          type="file"
          accept=".json"
          ref={fileInput}
          onChange={algOpen}
          style={{ display: "none" }}
        />
      </Box>
      <Stack direction="row" justifyContent="space-around" m={1}>
        <LargerTooltip
          title={
            physics ? "Disable physics simulation" : "Enable physics simulation"
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
          title={zoomView ? "Disable scroll to zoom" : "Enable scroll to Zoom"}
        >
          <Checkbox
            icon={<SearchIcon color="action" />}
            checked={zoomView}
            onChange={handleZoomView}
            checkedIcon={<BlockIcon color="action" />}
          />
        </LargerTooltip>
        <Button
          onClick={postAlgebraData}
          startIcon={<SyncIcon />}
          variant="contained"
        >
          Update
        </Button>
      </Stack>
      <NakayamaDialog
        open={openNakayama}
        setOpen={setOpenNakayama}
        handleCloseWithString={nakayamaClose}
      />
      <ReportDialog open={openReport} setOpen={setOpenReport} />
    </>
  );
}
