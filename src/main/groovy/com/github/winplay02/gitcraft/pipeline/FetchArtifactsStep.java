package com.github.winplay02.gitcraft.pipeline;

import com.github.winplay02.gitcraft.MinecraftVersionGraph;
import com.github.winplay02.gitcraft.mappings.MappingFlavour;
import com.github.winplay02.gitcraft.types.OrderedVersion;
import com.github.winplay02.gitcraft.util.GitCraftPaths;
import com.github.winplay02.gitcraft.util.RepoWrapper;

import java.nio.file.Path;

public class FetchArtifactsStep extends Step {

	private final Path rootPath;

	public FetchArtifactsStep(Path rootPath) {
		this.rootPath = rootPath;
	}

	public FetchArtifactsStep() {
		this(GitCraftPaths.MC_VERSION_STORE);
	}

	@Override
	public String getName() {
		return STEP_FETCH_ARTIFACTS;
	}

	@Override
	public boolean ignoresMappings() {
		return true;
	}

	@Override
	protected Path getInternalArtifactPath(OrderedVersion mcVersion, MappingFlavour mappingFlavour) {
		return this.rootPath.resolve(mcVersion.launcherFriendlyVersionName());
	}

	@Override
	public StepResult run(PipelineCache pipelineCache, OrderedVersion mcVersion, MappingFlavour mappingFlavour, MinecraftVersionGraph versionGraph, RepoWrapper repo) throws Exception {
		Path rootPath = getInternalArtifactPath(mcVersion, mappingFlavour);
		StepResult clientJar = null;
		StepResult serverJar = null;
		StepResult serverWindows = null;
		StepResult serverZip = null;
		if (mcVersion.hasClientCode()) {
			clientJar = mcVersion.clientJar().fetchArtifact(rootPath, "client jar");
		}
		if (mcVersion.hasServerCode()) {
			if (mcVersion.hasServerJar()) {
				serverJar = mcVersion.serverDist().serverJar().fetchArtifact(rootPath, "server jar");
			}
			if (mcVersion.hasServerWindows()) {
				serverWindows = mcVersion.serverDist().windowsServer().fetchArtifact(rootPath, "windows server");
			}
			if (mcVersion.hasServerZip()) {
				serverZip = mcVersion.serverDist().serverZip().fetchArtifact(rootPath, "server zip");
			}
		}
		return StepResult.merge(clientJar, serverJar, serverWindows, serverZip);
	}
}
