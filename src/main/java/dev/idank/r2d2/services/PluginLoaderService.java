package dev.idank.r2d2.services;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import dev.idank.r2d2.PluginLoader;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.PROJECT)
public final class PluginLoaderService {

    private PluginLoader pluginLoader;

    public PluginLoaderService(@NotNull Project project) {
        this.pluginLoader = new PluginLoader();
    }

    @NotNull
    public PluginLoader getPluginLoader() {
        return pluginLoader;
    }

}
